'use client'

import { BookOpen, Users, Target, Clock, ChevronRight } from 'lucide-react'
import { useApp } from '@/lib/app-context'
import { Card } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { BottomNav } from '@/components/bottom-nav'

export function HomeScreen() {
  const { state, setScreen } = useApp()

  return (
    <div className="min-h-screen bg-background pb-24">
      {/* Header */}
      <header className="px-5 pt-12 pb-6">
        <p className="text-sm text-muted-foreground mb-1">Assalamu&apos;alaikum</p>
        <h1 className="text-2xl font-semibold text-foreground">Selamat Pagi</h1>
      </header>

      {/* Main Content */}
      <main className="px-5 space-y-5">
        {/* Family Progress Card - Primary */}
        <Card className="relative overflow-hidden bg-primary text-primary-foreground p-5 rounded-3xl border-0">
          {/* Decorative pattern */}
          <div className="absolute top-0 right-0 w-32 h-32 opacity-10">
            <svg viewBox="0 0 100 100" className="w-full h-full">
              <pattern id="islamic" patternUnits="userSpaceOnUse" width="20" height="20">
                <circle cx="10" cy="10" r="1.5" fill="currentColor" />
              </pattern>
              <rect width="100" height="100" fill="url(#islamic)" />
            </svg>
          </div>

          <div className="relative z-10">
            <div className="flex items-center gap-2 mb-4">
              <div className="w-8 h-8 rounded-full bg-white/20 flex items-center justify-center">
                <Users className="w-4 h-4" />
              </div>
              <span className="text-sm font-medium opacity-90">{state.familyName}</span>
            </div>

            <div className="mb-4">
              <p className="text-xs opacity-75 mb-1">Progress saat ini</p>
              <h2 className="text-2xl font-semibold">
                {state.currentSurah} · Ayat {state.currentAyat}
              </h2>
            </div>

            <div className="flex items-center justify-between">
              <div className="flex items-center gap-1.5 text-xs opacity-80">
                <Clock className="w-3.5 h-3.5" />
                <span>Diupdate oleh {state.lastUpdatedBy}, {state.lastUpdatedTime}</span>
              </div>
            </div>

            <Button
              onClick={() => setScreen('reader')}
              className="w-full mt-5 h-12 bg-white/20 hover:bg-white/30 text-white rounded-xl font-medium backdrop-blur-sm border-0"
            >
              Lanjut Baca
            </Button>
          </div>
        </Card>

        {/* Quick Stats */}
        <div className="grid grid-cols-3 gap-3">
          <QuickCard
            icon={<BookOpen className="w-4 h-4 text-primary" />}
            label="Bookmark"
            value={state.personalBookmark ? `${state.personalBookmark.surah.substring(0, 8)}...` : '-'}
            onClick={() => setScreen('reader')}
          />
          <QuickCard
            icon={<Target className="w-4 h-4 text-accent" />}
            label="Target"
            value={state.targetKhatam.split(' ')[0]}
          />
          <QuickCard
            icon={<Users className="w-4 h-4 text-primary" />}
            label="Aktif"
            value={`${state.members.filter(m => m.lastSeen === 'Online').length} orang`}
            onClick={() => setScreen('family')}
          />
        </div>

        {/* Recent Activity */}
        <section>
          <div className="flex items-center justify-between mb-3">
            <h3 className="font-semibold text-foreground">Aktivitas Terbaru</h3>
            <button className="text-xs text-muted-foreground flex items-center gap-1">
              Lihat semua <ChevronRight className="w-3 h-3" />
            </button>
          </div>

          <Card className="rounded-2xl border border-border/50 divide-y divide-border/50">
            {state.activities.slice(0, 3).map(activity => (
              <ActivityItem key={activity.id} activity={activity} />
            ))}
          </Card>
        </section>

        {/* Daily Reminder */}
        <Card className="p-4 rounded-2xl bg-accent/20 border-0">
          <p className="text-sm text-foreground/80 text-center leading-relaxed">
            &quot;Sebaik-baik kalian adalah yang mempelajari Al-Quran dan mengajarkannya.&quot;
          </p>
          <p className="text-xs text-muted-foreground text-center mt-2">— HR. Bukhari</p>
        </Card>
      </main>

      <BottomNav />
    </div>
  )
}

function QuickCard({
  icon,
  label,
  value,
  onClick,
}: {
  icon: React.ReactNode
  label: string
  value: string
  onClick?: () => void
}) {
  return (
    <Card
      onClick={onClick}
      className={`p-3 rounded-2xl border border-border/50 ${onClick ? 'cursor-pointer active:scale-[0.98] transition-transform' : ''}`}
    >
      <div className="w-8 h-8 rounded-xl bg-secondary flex items-center justify-center mb-2">
        {icon}
      </div>
      <p className="text-xs text-muted-foreground">{label}</p>
      <p className="text-sm font-medium text-foreground truncate">{value}</p>
    </Card>
  )
}

function ActivityItem({ activity }: { activity: { member: string; action: string; surah: string; ayat: number; time: string } }) {
  return (
    <div className="flex items-center gap-3 p-3">
      <div className="w-9 h-9 rounded-full bg-secondary flex items-center justify-center text-sm font-medium text-foreground">
        {activity.member.charAt(0)}
      </div>
      <div className="flex-1 min-w-0">
        <p className="text-sm text-foreground">
          <span className="font-medium">{activity.member}</span>{' '}
          <span className="text-muted-foreground">{activity.action}</span>{' '}
          <span className="font-medium">{activity.surah} {activity.ayat}</span>
        </p>
        <p className="text-xs text-muted-foreground">{activity.time}</p>
      </div>
    </div>
  )
}
